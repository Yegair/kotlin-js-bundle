def artifactId = "foo"

def script = new File((File)basedir, "src/main/resources/${artifactId}.js").text
def bundle = new File((File)basedir, "target/classes/${artifactId}.bundle.js").text

if (bundle.indexOf(script) < 0) {
    throw new AssertionError("expected foo.js to be included in bundle")
}

return true