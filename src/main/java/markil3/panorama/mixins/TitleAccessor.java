package markil3.panorama.mixins;

import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TitleScreen.class)
public interface TitleAccessor
{
    @Accessor("backgroundRenderer")
    public void setPanoramic(RotatingCubeMapRenderer panoramic);
}
