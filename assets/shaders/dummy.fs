#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec4 u_color;

void main() {
    gl_FragColor = u_color;// * texture2D(u_texture, v_texCoords);
}