package markil3.panorama.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import markil3.panorama.EventBus;
import markil3.panorama.Main;

@Mixin(GameRenderer.class)
public class GameRenderHook
{
    @Inject(method = "render", at = @At(value = "HEAD"))
    public void startPanora(float tickDelta, long startTime, boolean tick,
                            CallbackInfo callbackInfo)
    {
        if (Main.isRenderingPanorama())
        {
            Main.renderPanorama(MinecraftClient.getInstance());
        }
    }

    @Inject(method = "renderWorld", at = @At(value = "HEAD"))
    public void startPanora(float tickDelta,
                            long limitTime,
                            MatrixStack matrix,
                            CallbackInfo callbackInfo)
    {
        if (Main.isRenderingPanorama())
        {
            ((GameRenderAccessor) MinecraftClient.getInstance().gameRenderer).setPanoramic(
                    true);
        }
    }
    @Inject(method = "renderWorld", at = @At(value = "TAIL"))
    public void finishPanora(float tickDelta,
                            long limitTime,
                            MatrixStack matrix,
                            CallbackInfo callbackInfo)
    {
        if (Main.isRenderingPanorama())
        {
            ((GameRenderAccessor) MinecraftClient.getInstance().gameRenderer).setPanoramic(
                    false);
        }
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet" +
            "/minecraft/client/render/Camera;update" +
            "(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;" +
            "ZZF)V", shift = At.Shift.AFTER))
    public void adjustCamera(float tickDelta,
                             long limitTime,
                             MatrixStack matrix,
                             CallbackInfo callbackInfo)
    {
        if (Main.isRenderingPanorama())
        {
            EventBus.onCamera(MinecraftClient.getInstance().gameRenderer.getCamera());
        }
    }
}
