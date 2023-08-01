package robaertschi.imguirenderer.imgui;

import java.util.ArrayList;
import java.util.Objects;

import org.lwjgl.glfw.GLFW;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.Minecraft;

public class ImGUIRenderer {
	private static ImGUIRenderer INSTANCE = null;
	
	public static ImGUIRenderer getInstance() {
		if(INSTANCE == null) INSTANCE = new ImGUIRenderer();
		return INSTANCE;
	}
	private final ArrayList<ImGUICall> drawCalls = new ArrayList<>();
	private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
	private final ImGuiImplGl3 imGuiGl = new ImGuiImplGl3();
	
	private ImGUIRenderer() {
	}
	
	public void init(ImGUICall config) {
		ImGui.createContext();
		config.execute();
		imGuiGlfw.init(Minecraft.getInstance().getWindow().getWindow(), false);
		imGuiGl.init();
	}

	public void draw(ImGUICall drawCall) {
		drawCalls.add(drawCall);
	}
	
	public void render() {
		imGuiGlfw.newFrame();
		ImGui.newFrame();
		
		for(ImGUICall drawCall : drawCalls) {
			drawCall.execute();
		}
		drawCalls.clear();
		
		ImGui.render();
		imGuiGl.renderDrawData(Objects.requireNonNull(ImGui.getDrawData()));
		
		if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
			final long backupWindowPtr = GLFW.glfwGetCurrentContext();
			ImGui.updatePlatformWindows();
			ImGui.renderPlatformWindowsDefault();
			GLFW.glfwMakeContextCurrent(backupWindowPtr);
		}
	}
}
