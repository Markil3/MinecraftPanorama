package markil3.panorama.mixins;

import net.minecraft.client.Keyboard;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.function.Consumer;

import markil3.panorama.Main;

@Mixin(ScreenshotUtils.class)
public class ScreenshotHook
{
    @Inject(method = "saveScreenshot", at = @At(value = "HEAD"))
    private static void takeScreenshot(File gameDirectory,
                                       int framebufferWidth,
                                       int framebufferHeight,
                                       Framebuffer framebuffer,
                                       Consumer<Text> messageReceiver,
                                       CallbackInfo callbackInfo)
    {
        Main.startRenderingPanorama();
    }
}
