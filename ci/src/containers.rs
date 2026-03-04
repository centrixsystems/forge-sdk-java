use dagger_sdk::{Container, Directory, Query};

/// Base Maven container with local repository cache.
pub fn maven_builder(client: &Query, source: Directory) -> Container {
    let m2_cache = client.cache_volume("forge-sdk-java-m2");

    client
        .container()
        .from("maven:3.9-eclipse-temurin-11")
        .with_mounted_directory("/build", source)
        .with_workdir("/build")
        .with_mounted_cache("/root/.m2/repository", m2_cache)
}
