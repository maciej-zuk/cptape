#ifdef GL_ES
    precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec4 u_color;

void main() {
	vec2 mb=v_texCoords-vec2(0.5,0.5);
	float mbo=1.0/sqrt(mb.x*mb.x+mb.y*mb.y);
	gl_FragColor=v_color;
	gl_FragColor.a=smoothstep(2.0, 2.05, mbo);
}