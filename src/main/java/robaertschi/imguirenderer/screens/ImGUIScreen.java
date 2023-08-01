package robaertschi.imguirenderer.screens;

import com.mojang.blaze3d.vertex.PoseStack;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ImGUIScreen extends Screen {

	private static ImGUIScreen INSTANCE = null;

	public static ImGUIScreen getInstance() {
		if(INSTANCE == null) { INSTANCE = new ImGUIScreen(); }
		return INSTANCE;
	}

	private ImGUIScreen() {
		super(Component.literal("ImGUI"));
	}
	
	public void init() {
		ImGui.getIO().removeConfigFlags(ImGuiConfigFlags.NoMouse);
	}


	@Override
	public void removed() {
		ImGui.getIO().addConfigFlags(ImGuiConfigFlags.NoMouse);
	}
}
