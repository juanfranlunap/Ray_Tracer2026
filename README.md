# Ray Tracer v1.0
**Multimedia & Computer Graphics** — Universidad Panamericana 2026

A Java-based ray tracer built progressively through the course, implementing core rendering techniques from scratch.

## Features
- **Blinn-Phong shading** — ambient, diffuse, and specular lighting
- **Point lights & directional lights** — with inverse square law falloff
- **Shadow rays** — hard shadows with self-intersection prevention
- **Reflection** — recursive ray bouncing up to N depth
- **Refraction** — Snell's law with index of refraction support
- **Phong normal interpolation** — smooth shading across triangle meshes
- **OBJ file loading** — with automatic normalization and scale/offset control
- **BVH acceleration** — Bounding Volume Hierarchy for fast ray-triangle intersection
- **Multithreaded rendering** — parallel row processing using Java ExecutorService

## Scenes
### Scene 1 — Yahuarcocha
Representation of the historic Battle of Yahuarcocha featuring an Inca warrior, skulls, and a reflective blood lake.

### Scene 2 — Skate Park
Urban skate scene with a ramp, skateboard, and spray cans demonstrating diffuse and specular materials.

### Scene 3 — Glass Chess
Chess pieces made of glass on a reflective water-like board, showcasing reflection and refraction.

## How to Run
```bash
javac -d bin src/*.java
java -cp bin Main
```

## Tech Stack
- Java 21+
- No external libraries — pure Java implementation
