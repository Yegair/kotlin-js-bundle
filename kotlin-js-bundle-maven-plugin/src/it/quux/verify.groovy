def artifactId = "quux"

def alphabet = new File((File)basedir, "src/main/resources/alphabet.js").text
def script = new File((File)basedir, "src/main/resources/${artifactId}.js").text
def bundle = new File((File)basedir, "target/js/${artifactId}.bundle.js").text

if (bundle.indexOf(alphabet) < 0) {
    throw new AssertionError("expected alphabet.js to be included in bundle")
}

if (bundle.indexOf(script) < bundle.indexOf(alphabet)) {
    throw new AssertionError("expected quux.js to be included in bundle after alphabet.js")
}

return true