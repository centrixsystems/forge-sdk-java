use dagger_sdk::{Directory, Query};
use eyre::WrapErr;

use crate::containers::maven_builder;

/// Run Maven tests.
pub async fn run(client: &Query, source: Directory) -> eyre::Result<String> {
    let output = maven_builder(client, source)
        .with_exec(vec!["mvn", "test"])
        .with_exec(vec!["sh", "-c", "echo 'test: all tests passed'"])
        .stdout()
        .await
        .wrap_err("test failed")?;

    Ok(output)
}
