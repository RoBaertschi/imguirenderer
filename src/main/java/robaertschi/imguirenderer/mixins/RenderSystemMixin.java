package robaertschi.imguirenderer.mixins;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;

import robaertschi.imguirenderer.imgui.ImGUIRenderer;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;

@Mixin(RenderSystem.class)
public abstract class RenderSystemMixin {

	@Shadow @Final
	static Logger LOGGER;

	@Inject( at = @At( value = "TAIL" ), method = "initRenderer(IZ)V" )
	private static void imguirenderer$initRenderer(int flags, boolean enable, CallbackInfo cbi) {
		RenderSystem.assertOnRenderThread();
		LOGGER.info("Starting ImGUI");
		ImGUIRenderer.getInstance().init(()->{
			ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable);
			ImGui.getIO().addConfigFlags(ImGuiConfigFlags.NoMouse);
		});
	}
	
	@Inject( at = @At( value = "HEAD"), method = "flipFrame(J)V" )
	private static void imguirenderer$flipFrame(long p_69496_, CallbackInfo cbi) {
		RenderSystem.recordRenderCall(() -> {
			ImGUIRenderer.getInstance().render();
		});
	}
}
