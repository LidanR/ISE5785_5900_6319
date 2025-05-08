package primitives;

public class Material {
    /// Diffuse reflection coefficient
    public Double3 Ka = Double3.ONE; // Ambient reflection coefficient

    public Material setKa(Double3 Ka) {
        this.Ka = Ka;
        return this;
    }

    
    public Material setKa(double Ka) {
        this.Ka = new Double3(Ka);
        return this;
    }
}
