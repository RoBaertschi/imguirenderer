import magik.createGithubPublication
import magik.github
import java.util.Date
import java.text.SimpleDateFormat
plugins {
	`maven-publish`
	`eclipse`
	id("net.neoforged.gradle") version "[6.0.18,6.2)"
	id("org.spongepowered.mixin") version "0.7.+"
	id("elect86.magik") version "0.3.2"
}

val mod_version: String by extra
val mod_group_id: String by extra
val mod_id: String by extra
val mod_name: String by extra
val mod_license: String by extra
val mod_authors: String by extra
val mod_description: String by extra
val mapping_channel: String by extra
val mapping_version: String by extra
val minecraft_version: String by extra
val minecraft_version_range: String by extra
val neo_version: String by extra
val neo_version_range: String by extra
val loader_version_range: String by extra
val imgui_version: String by extra
val pack_format_number: String by extra

version = mod_version
group = mod_group_id

base {
	archivesName.set(mod_id)
}

//java.toolchain.languageVersion = JavaLanguageVersion.of(17)
java.toolchain {
	languageVersion.set(JavaLanguageVersion.of(17))
}
println("Java: ${System.getProperty("java.version")}, JVM: ${System.getProperty("java.vm.version")} (${System.getProperty("java.vendor")}), Arch: ${System.getProperty("os.arch")}")
jarJar.enable()
jarJar {}


repositories {
	mavenCentral()
	maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
}

minecraft {
	mappings(mapping_channel, mapping_version)
	//copyIdeResources = true
	copyIdeResources.set(true)
	runs {
		configureEach {
			workingDirectory(project.file("run"))
			property("forge.logging.markers", "REGISTRIES")
			property("forge.logging.console.level", "debug")
			mods {
				create(mod_id) {
					source (sourceSets.main.get())
				}
			}
		}

		create("client") {
			property("forge.enabledGameTestNamespaces", mod_id.toString())
		}
		create("server") {
			property("forge.enabledGameTestNamespaces", mod_id.toString())
		}
		create("gameTestServer") {
			property("forge.enabledGameTestNamespaces", mod_id.toString())
		}

		create("data") {
			workingDirectory(project.file("run-data"))
			args("--mod", mod_id, "--all", "--output", file("src/generated/resources"), "--existing", file("src/main/resources"))
		}

	}
}

sourceSets {
	main {
		resources {
			srcDir("src/generated/resources")
		}
	}
}

dependencies {
	minecraft("net.neoforged:forge:${minecraft_version}-${neo_version}")
	annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
	
	for (it in listOf("binding", "lwjgl3", "natives-linux", "natives-windows")) {
		minecraftLibrary (group = "io.github.spair", name = "imgui-java-$it", version = imgui_version) {
			exclude(group = "org.lwjgl")
		}
		jarJar(group = "io.github.spair", name= "imgui-java-${it}", version= "[0,)") {
			//transitive(false)
			isTransitive = false
			exclude(group = "org.lwjgl")
		}
	}
}

tasks.withType<ProcessResources>().configureEach {
	var replaceProperties = mapOf(
	        "minecraft_version"	to minecraft_version, 		"minecraft_version_range" to minecraft_version_range,
			"neo_version"			to neo_version,			"neo_version_range" to neo_version_range,
			"loader_version_range" 	to loader_version_range,
			"mod_id"				to mod_id, "mod_name" to mod_name, "mod_license" to mod_license, "mod_version" to mod_version,
			"mod_authors"			to mod_authors, "mod_description" to mod_description, "pack_format_number" to pack_format_number,
	)
	inputs.properties (replaceProperties)

	filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
		expand (replaceProperties + mapOf("project" to project))
	}
}

mixin {
	config("imguirenderer.mixin.json")
}

tasks.jar {
	manifest {
		attributes(mapOf(
				"Specification-Title"     to mod_id,
				"Specification-Vendor"    to mod_authors,
				"Specification-Version"   to "1",
				"Implementation-Title"    to project.name,
				"Implementation-Version"  to version,
				"Implementation-Vendor"   to mod_authors,
				"Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
				"MixinConfigs"			  to "forge-mixin.json"
		))
	}
	finalizedBy("reobfJar")
}

publishing {
	publications {
		register<MavenPublication>("gpr") {
			from(components["java"])

		}

//		github(GithubSnapshotPublication) {
//			from(components.java)
//		}

		createGithubPublication("github") {
			from(components["java"])
		}


	}
	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/RoBaertschi/imguirenderer")
			credentials {
				username = System.getenv("USERNAME")
				password = System.getenv("TOKEN")
			}
		}

		github {
			domain = "RoBaertschi/mirror"
		}

	}
}

// Only allow publishing to their respective repositories
tasks.withType<PublishToMavenRepository>().configureEach {
	val predicate = provider {
		(repository == publishing.repositories["GitHubPackages"] &&
				publication == publishing.publications["gpr"]) ||
		(repository == publishing.repositories["github"] &&
				publication == publishing.publications["github"])
	}
	onlyIf("publishing GitHub Packages only to gpr and publishing Github only to github") {
		predicate.get()
	}
}

tasks.withType<PublishToMavenLocal>().configureEach {
	val predicate = provider {
		publication == publishing.publications["github"]
	}
	onlyIf("only publish github to maven local") {
		predicate.get()
	}
}

magik {
	verbose.convention(true)
}

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
}
