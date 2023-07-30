package robaertschi.imguirenderer.screens;

import com.mojang.blaze3d.vertex.PoseStack;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ImGuiScreen extends Screen{
	
	private static ImGuiScreen _INSTANCE = null;
	
	public static ImGuiScreen getInstance() {
		if(_INSTANCE == null) { _INSTANCE = new ImGuiScreen(); }
		return _INSTANCE;
	}

	private ImGuiScreen() {
		super(Component.literal("ImGui"));
	}
	
	public void init() {
		ImGui.getIO().removeConfigFlags(ImGuiConfigFlags.NoMouse);
	}
	
	public void render(PoseStack proseStack, int x, int y, float partialTicks) {

	}

	@Override
	public void removed() {
		ImGui.getIO().addConfigFlags(ImGuiConfigFlags.NoMouse);
	}
}
