def artifactId = "bar"

def script = new File(basedir, "src/main/resources/script.js").text
def bundle = new File(basedir, "target/js/${artifactId}.bundle.js").text

return bundle == script