package markil3.panorama.mixins;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRenderAccessor
{
    @Accessor("renderingPanorama")
    public void setPanoramic(boolean panoramic);
}
