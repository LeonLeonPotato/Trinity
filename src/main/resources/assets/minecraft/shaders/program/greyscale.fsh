#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

void main() {
    mainImage(gl_FragColor);
}

void mainImage( out vec4 fragColor )
{
    vec4 tex = texture2D(DiffuseSampler, texCoord);
    vec3 greyScale = vec3(.5, .5, .5);
    fragColor = vec4( vec3(dot( tex.rgb, greyScale)), tex.a);
}