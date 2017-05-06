def artifactId = "project-with-js-dependency"

def fooJs = new File(basedir, "../js-project/src/main/resources/foo.js").text
def bundleJs = new File(basedir, "target/js/${artifactId}.bundle.js").text
return bundleJs == fooJs