package markil3.panorama;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Main implements ModInitializer, IdentifiableResourceReloadListener
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    static int numPanoramas = 0;

    private static int screenshotState = 0;

    @Override
    public void onInitialize()
    {
//        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
//                .registerReloadListener(this);
    }

    public Main()
    {
    }

    static void loadPanoramas(MinecraftClient mc,
                              ResourceManager manager,
                              Executor prepareExecutor,
                              Executor applyExecutor)
    {
        int i = 0;
        int j;
        TextureManager textureManager = mc.getTextureManager();
        File panoramas = new File(mc.runDirectory, "panoramas");
        if (panoramas.isDirectory())
        {
            for (File panorama : panoramas.listFiles())
            {
                if (panorama.isDirectory())
                {
                    for (j = 0; j < 6; j++)
                    {
                        int finalI = i;
                        int finalJ = j;
                        Identifier loc = new Identifier(
                                "panorama:textures/gui/title" +
                                        "/background/" + finalI +
                                        "/panorama_" + finalJ + ".png");
                        textureManager.registerTexture(loc,
                                new FileTexture(loc,
                                        new File(panorama,
                                                "panorama_" + finalJ +
                                                        ".png")));
                    }
                    i++;
                }
            }
        }
        numPanoramas = i;
    }

    public static boolean isRenderingPanorama()
    {
        return screenshotState > 0;
    }

    public static int getPanoramaStage()
    {
        return screenshotState;
    }

    public static int nextStage()
    {
        return ++screenshotState;
    }

    public static void startRenderingPanorama()
    {
        screenshotState = 1;
    }

    public static void finishRenderingPanorama()
    {
        screenshotState = 0;
    }

    public static RotatingCubeMapRenderer getPanorama()
    {
        if (numPanoramas > 0)
        {
            int panorama =
                    MathHelper.floor(Math.random() * Main.numPanoramas);
            return new RotatingCubeMapRenderer(new CubeMapRenderer(new Identifier(
                    "panorama:textures/gui/title/background/" + panorama +
                            "/panorama")));
        }
        return null;
    }

    public static void renderPanorama(MinecraftClient mc)
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
                mc.gameRenderer.renderWorld(mc.getTickDelta(),
                        Util.getMeasuringTimeNano(), new MatrixStack());
                saveScreenshot(mc, name, i);
                nextStage();
            }
            finishRenderingPanorama();
        }
    }

    static void saveScreenshot(MinecraftClient mc,
                               String panoramaNum,
                               int panoramaSide)
    {
        final int SCREENSHOT_WIDTH = 2048;
        final int SCREENSHOT_HEIGHT = 2048;

        File file = new File(mc.runDirectory,
                "panoramas/" + panoramaNum + "/panorama_" + panoramaSide +
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
        NativeImage nativeimage = ScreenshotUtils.takeScreenshot(
                SCREENSHOT_WIDTH,
                SCREENSHOT_HEIGHT,
                mc.getFramebuffer());
        Util.getIoWorkerExecutor().execute(() -> {
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
                nativeimage1.writeFile(file);
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

    @Override
    public Identifier getFabricId()
    {
        return new Identifier("panorama", "backgrounds");
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer,
                                          ResourceManager manager,
                                          Profiler prepareProfiler,
                                          Profiler applyProfiler,
                                          Executor prepareExecutor,
                                          Executor applyExecutor)
    {
        loadPanoramas(MinecraftClient.getInstance(),
                manager,
                prepareExecutor,
                applyExecutor);
        return CompletableFuture.completedFuture(null);
    }
}
