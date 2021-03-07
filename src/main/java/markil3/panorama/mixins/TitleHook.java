package markil3.panorama.mixins;

import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import markil3.panorama.Main;

@Mixin(TitleScreen.class)
public class TitleHook
{
    @Redirect(method = "<init>(Z)V", at = @At(value = "FIELD", target = "Lnet" +
            "/minecraft/client/gui/screen/TitleScreen;" +
            "backgroundRenderer:Lnet/minecraft/client/gui" +
            "/RotatingCubeMapRenderer;", opcode = Opcodes.PUTFIELD))
    private void injected(TitleScreen screen,
                          RotatingCubeMapRenderer defaultPan)
    {
        RotatingCubeMapRenderer pan = Main.getPanorama();
        if (pan != null)
        {
            ((TitleAccessor) screen).setPanoramic(pan);
        }
        else
        {
            ((TitleAccessor) screen).setPanoramic(defaultPan);
        }
    }
}
