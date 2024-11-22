plugins {
	id("dev.architectury.loom")
	id("checkstyle")
}

repositories {
	maven(url = "https://maven.neoforged.net/releases/")
}

base.archivesName.set("${project.properties["archives_base_name"]}")
version = "${project.properties["mod_version"]}-${project.properties["minecraft_version"]}-neoforge"
group = "${project.properties["maven_group"]}"

evaluationDependsOn(":Common")

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
	minecraft("com.mojang:minecraft:${project.properties["minecraft_version"]}")
	mappings("net.fabricmc:yarn:${project.properties["yarn_mappings"]}:v2")

	neoForge("net.neoforged:neoforge:${project.properties["neoforge_version"]}")

	compileOnly("io.github.llamalad7:mixinextras-common:${project.properties["mixinextras_version"]}")
	annotationProcessor("io.github.llamalad7:mixinextras-common:${project.properties["mixinextras_version"]}")
	implementation("io.github.llamalad7:mixinextras-neoforge:${project.properties["mixinextras_version"]}")
	include("io.github.llamalad7:mixinextras-neoforge:${project.properties["mixinextras_version"]}")

	implementation(project(path = ":Common", configuration = "namedElements"))
}

loom {
	mixin.useLegacyMixinAp = false
}

tasks.check {
	dependsOn(project(":Common").tasks.check)
}

tasks.processResources {
	from(project(":Common").sourceSets.main.get().resources)

	inputs.property("version", project.properties["mod_version"])
	filesMatching("META-INF/mods.toml") {
		expand(mapOf("version" to project.properties["mod_version"]))
	}
}

tasks.compileJava {
	source(project(":Common").sourceSets.main.get().java)
}