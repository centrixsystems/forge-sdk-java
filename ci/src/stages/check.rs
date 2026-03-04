use dagger_sdk::{Directory, Query};
use eyre::WrapErr;

use crate::containers::maven_builder;

/// Compile the project with Maven.
pub async fn run(client: &Query, source: Directory) -> eyre::Result<String> {
    let output = maven_builder(client, source)
        .with_exec(vec!["mvn", "compile", "-q"])
        .with_exec(vec!["sh", "-c", "echo 'check: mvn compile passed'"])
        .stdout()
        .await
        .wrap_err("check failed")?;

    Ok(output)
}
