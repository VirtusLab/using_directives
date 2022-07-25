with import <nixpkgs> {};

stdenv.mkDerivation {
  name = "using_directives";

  buildInputs = with pkgs; [
    gradle_6
  ];
}
