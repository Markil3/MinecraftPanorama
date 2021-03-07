package markil3.panorama.mixins;

import net.minecraft.client.render.Camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor
{
    @Invoker("setRotation")
    public void setView(float yaw, float pitch);
}
