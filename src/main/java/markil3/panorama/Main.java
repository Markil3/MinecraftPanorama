package markil3.panorama;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Util;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
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
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("panorama")
public class Main
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

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

        // Register ourselves for server and other game events we are
        // interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}",
                event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other
        // mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event)
    {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on
    // the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
        {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }

    static void renderPanorama(Minecraft mc)
    {
        final int SCREENSHOT_WIDTH = 2048;
        final int SCREENSHOT_HEIGHT = 2048;

        RenderSystem.viewport(0, 0, SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);

        if (mc.world != null)
        {
            // We need to render an extra frame at the beginning, or
            // otherwise it won't render correctly.
            for (int i = -1; i < 6; i++)
            {
                mc.gameRenderer.renderWorld(mc.getRenderPartialTicks(),
                        Util.nanoTime(), new MatrixStack());
                saveScreenshot(mc, 0, i);
            }
        }
    }

    static void saveScreenshot(Minecraft mc, int panoramaNum, int panoramaSide)
    {
        final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
        final int FRAMEBUFFER_WIDTH = mc.getMainWindow().getFramebufferWidth();
        final int FRAMEBUFFER_HEIGHT =
                mc.getMainWindow().getFramebufferHeight();
        final int SCREENSHOT_WIDTH = 2048;
        final int SCREENSHOT_HEIGHT = 2048;

        File file = new File(mc.gameDir,
                "panoramas/" + FORMATTER.format(new Date()) + "/panorama_" + panoramaSide +
                        ".png");
        file.getParentFile().mkdirs();
        try
        {
            file.createNewFile();
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
