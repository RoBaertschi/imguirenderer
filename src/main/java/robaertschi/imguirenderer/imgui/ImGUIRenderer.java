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
	private static ImGUIRenderer _INSTANCE = null;
	
	public static ImGUIRenderer getInstance() {
		if(_INSTANCE == null) _INSTANCE = new ImGUIRenderer();
		return _INSTANCE;
	}
	
	private ArrayList<ImGUICall> _preDrawCalls = new ArrayList<>();
	private ArrayList<ImGUICall> _drawCalls = new ArrayList<>();
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
	
	public void preDraw(ImGUICall drawCall) {
		_preDrawCalls.add(drawCall);
	}
	
	public void draw(ImGUICall drawCall) {
		_drawCalls.add(drawCall);
	}
	
	public void render() {
		for(ImGUICall preDrawCall : _preDrawCalls) {
			preDrawCall.execute();
		}
		_preDrawCalls.clear();
		
		imGuiGlfw.newFrame();
		ImGui.newFrame();
		
		// Render ImGui Here
		for(ImGUICall drawCall : _drawCalls) {
			drawCall.execute();
		}
		_drawCalls.clear();
		
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
