def artifactId = "quuz"

def script = new File((File)basedir, "src/main/resources/${artifactId}.js").text
def bundle = new File((File)basedir, "target/js/${artifactId}.bundle.js").text

def testScript = new File((File)basedir, "src/test/resources/${artifactId}-tests.js").text
def testBundle = new File((File)basedir, "target/test-js/${artifactId}-tests.bundle.js").text

if (bundle.indexOf(script) < 0) {
    throw new AssertionError("expected bundle to contain script")
}

if (testBundle.indexOf(script) < 0) {
    throw new AssertionError("expected test bundle to contain script")
}

if (testBundle.indexOf(script) >= testBundle.indexOf(testScript)) {
    throw new AssertionError("expected test bundle to contain test script after script")
}

return true