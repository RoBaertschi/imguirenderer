package robaertschi.imguirenderer;

import com.mojang.logging.LogUtils;
import imgui.ImGui;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import robaertschi.imguirenderer.imgui.ImGUIRenderer;
import robaertschi.imguirenderer.screens.ImGUIScreen;

@Mod(ImGUIRendererMod.MODID)
public class ImGUIRendererMod {
	public static final String MODID = "imguirenderer";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final KeyMapping OPEN_IMGUI_SCREEN = new KeyMapping("key.imguirenderer.open_imgui_screen", GLFW.GLFW_KEY_L, "key.categories.imguirenderer");

	public ImGUIRendererMod(){
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event){
		LOGGER.info("HELLO from server starting");
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents{

		static {
			MinecraftForge.EVENT_BUS.addListener(ClientModEvents::onClientRenderTick);
			MinecraftForge.EVENT_BUS.addListener(ClientModEvents::onClientTick);
		}

		@SubscribeEvent
		public static void onRegisterKeymapping(RegisterKeyMappingsEvent event) {
			event.register(OPEN_IMGUI_SCREEN);
		}

		public static void onClientRenderTick(TickEvent.RenderTickEvent event) {
			if (event.phase == TickEvent.Phase.START) return;
			if (Minecraft.getInstance().level == null) return;
			if (Minecraft.getInstance().screen == ImGUIScreen.getInstance() || Minecraft.getInstance().screen == null) {
				ImGUIRenderer.getInstance().draw(() -> {
					ImGui.begin("Hello World!");
					ImGui.text("Moin");
					ImGui.end();
				});
			}
		}

		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (event.phase == TickEvent.Phase.END && OPEN_IMGUI_SCREEN.isDown() && Minecraft.getInstance().player != null && (Minecraft.getInstance().screen != ImGUIScreen.getInstance())) {
				Minecraft.getInstance().setScreen(ImGUIScreen.getInstance());
			}
		}
	}
}
