def artifactId = "quuxquuz"

def foo = new File((File)basedir, "../foo/src/main/resources/foo.js").text
def bar = new File((File)basedir, "../bar/src/main/resources/bar.js").text

def foobar = new File((File)basedir, "../foobar/src/main/resources/foobar.js").text

def alphabet = new File((File)basedir, "../quux/src/main/resources/alphabet.js").text
def quux = new File((File)basedir, "../quux/src/main/resources/quux.js").text

def script = new File((File)basedir, "src/main/resources/${artifactId}.js").text
def bundle = new File((File)basedir, "target/js/${artifactId}.bundle.js").text

def testScript = new File((File)basedir, "src/test/resources/${artifactId}-tests.js").text
def testBundle = new File((File)basedir, "target/test-js/${artifactId}-tests.bundle.js").text

if (bundle.indexOf(foo) >= 0) {
    throw new AssertionError("expected foo.js with scope = provided not to be included in bundle")
}

if (bundle.indexOf(foobar) >= 0) {
    throw new AssertionError("expected foobar.js with scope = test not to be included in bundle")
}

if (bundle.indexOf(alphabet) < 0) {
    throw new AssertionError("expected alphabet.js to be included in bundle")
}

if (bundle.indexOf(quux) < bundle.indexOf(alphabet)) {
    throw new AssertionError("expected quux.js to be included in bundle after alphabet.js")
}

if (bundle.indexOf(script) < bundle.indexOf(quux)) {
    throw new AssertionError("expected quuxquuz.js to be included in bundle after quux.js")
}

if (testBundle.indexOf(foo) < 0) {
    throw new AssertionError("expected foo.js with transitive scope = test to be included in test bundle")
}

if (testBundle.indexOf(bar) < 0) {
    throw new AssertionError("expected bar.js with transitive scope = test to be included in test bundle")
}

if (testBundle.indexOf(foobar) < testBundle.indexOf(foo)) {
    throw new AssertionError("expected foobar.js to be included in test bundle before foo.js")
}

if (testBundle.indexOf(foobar) < testBundle.indexOf(bar)) {
    throw new AssertionError("expected foobar.js to be included in test bundle before bar.js")
}

if (testBundle.indexOf(alphabet) < 0) {
    throw new AssertionError("expected alphabet.js to be included in test bundle")
}

if (testBundle.indexOf(quux) < testBundle.indexOf(alphabet)) {
    throw new AssertionError("expected quux.js to be included in test bundle after alphabet.js")
}

if (testBundle.indexOf(script) < testBundle.indexOf(quux)) {
    throw new AssertionError("expected quuxquuz.js to be included in test bundle after quux.js")
}

if (testBundle.indexOf(testScript) < testBundle.indexOf(script)) {
    throw new AssertionError("expected quuxquuz-tests.js to be included in test bundle after quuxquuz.js")
}

return true