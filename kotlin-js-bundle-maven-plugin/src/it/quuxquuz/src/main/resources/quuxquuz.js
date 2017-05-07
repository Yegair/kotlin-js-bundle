function quuxquuz() {
    if (foo() === "foo") {
        return quux() + quuz();
    } else {
        throw "expected foo() to be 'foo'";
    }
}