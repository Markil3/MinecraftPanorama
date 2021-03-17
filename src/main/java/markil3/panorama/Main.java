package markil3.panorama;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Util;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("panorama")
public class Main
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    static int numPanoramas = 0;

    public Main()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get()
                .getModEventBus()
                .addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get()
                .getModEventBus()
                .addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get()
                .getModEventBus()
                .addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get()
                .getModEventBus()
                .addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get()
                .getModEventBus()
                .addListener(this::onStitch);

        // Register ourselves for server and other game events we are
        // interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onStitch(ModelBakeEvent event)
    {
        Main.loadPanoramas(Minecraft.getInstance());
    }

    static void loadPanoramas(Minecraft mc)
    {
        int i = 0;
        int j;
        TextureManager textureManager = mc.textureManager;
        ResourceLocation loc;
        File panoramas = new File(mc.gameDir, "panoramas");
        if (panoramas.isDirectory())
        {
            for (File panorama : Optional.ofNullable(panoramas.listFiles()).orElse(new File[0]))
            {
                if (panorama.isDirectory())
                {
                    for (j = 0; j < 6; j++)
                    {
                        loc = new ResourceLocation(
                                "panorama:textures/gui/title/background/" + i + "/panorama_" + j + ".png");
                        textureManager.loadTexture(loc,
                                new FileTexture(loc,
                                        new File(panorama,
                                                "panorama_" + j + ".png")));
                    }
                    i++;
                }
            }
        }
        numPanoramas = i;
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
//        loadPanoramas(event.getMinecraftSupplier().get());
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
    }

    private void processIMC(final InterModProcessEvent event)
    {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event)
    {
    }

    static void renderPanorama(Minecraft mc)
    {
        final SimpleDateFormat FORMATTER =
                new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

        final int SCREENSHOT_WIDTH = 2048;
        final int SCREENSHOT_HEIGHT = 2048;

        String name;

        RenderSystem.viewport(0, 0, SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);

        if (mc.world != null)
        {
            name = FORMATTER.format(new Date());
            // We need to render an extra frame at the beginning, or
            // otherwise it won't render correctly.
            for (int i = -1; i < 6; i++)
            {
                mc.gameRenderer.renderWorld(mc.getRenderPartialTicks(),
                        Util.nanoTime(), new MatrixStack());
                saveScreenshot(mc, name, i);
            }
        }
    }

    static void saveScreenshot(Minecraft mc, String name, int panoramaSide)
    {
        final int FRAMEBUFFER_WIDTH = mc.getMainWindow().getFramebufferWidth();
        final int FRAMEBUFFER_HEIGHT =
                mc.getMainWindow().getFramebufferHeight();
        final int SCREENSHOT_WIDTH = 1024;
        final int SCREENSHOT_HEIGHT = 1024;

        File file = new File(mc.gameDir,
                "panoramas/" + name + "/panorama_" + panoramaSide +
                        ".png");
        try
        {
            if (!file.getParentFile().mkdirs())
            {
                throw new IOException("Could not create panorama directory " + file.getParent());
            }
            if (!file.createNewFile())
            {
                throw new IOException("Could not create file " + file.getAbsolutePath());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        NativeImage nativeimage = ScreenShotHelper.createScreenshot(
                SCREENSHOT_WIDTH,
                SCREENSHOT_HEIGHT,
                mc.getFramebuffer());
        Util.getRenderingService().execute(() -> {
            int i = nativeimage.getWidth();
            int j = nativeimage.getHeight();
            int k = 0;
            int l = 0;
            if (i > j)
            {
                k = (i - j) / 2;
                i = j;
            }
            else
            {
                l = (j - i) / 2;
                j = i;
            }

            try (NativeImage nativeimage1 = new NativeImage(SCREENSHOT_WIDTH,
                    SCREENSHOT_HEIGHT,
                    false))
            {
                nativeimage.resizeSubRectTo(k, l, i, j, nativeimage1);
                nativeimage1.write(file);
            }
            catch (IOException ioexception)
            {
                LOGGER.warn("Couldn't save auto screenshot",
                        (Throwable) ioexception);
            }
            finally
            {
                nativeimage.close();
            }

        });
    }
}
