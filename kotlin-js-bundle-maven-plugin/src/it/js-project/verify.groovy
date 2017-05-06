def artifactId = "js-project"

def fooJs = new File(basedir, "src/main/resources/foo.js").text
def bundleJs = new File(basedir, "target/js/${artifactId}.bundle.js").text
return bundleJs == fooJs