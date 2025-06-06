package primitives;

public class Material {
    // Ambient reflection coefficient
    public Double3 Ka = Double3.ONE;
    // Diffuse reflection coefficient
    public Double3 Kd = Double3.ZERO;
    // Specular reflection coefficient
    public Double3 Ks = Double3.ZERO;
    // Transparency coefficient
    public Double3 Kt = Double3.ZERO;
    // Refraction coefficient
    public Double3 Kr = Double3.ZERO;
    // Shininess coefficient
    public int nSh = 0;
    // strength of the material
    public double strength = 0.5;

    private final double maxDegree = 90;

    /**
     * setter for the strength of the material.
     * @param strength the strength of the material
     * @return this Material object
     */
    public Material setStrength(double strength) {
        this.strength = strength%maxDegree;
        return this;
    }
    /**
     * Setter for the ambient reflection coefficient.
     * @param Ka the ambient reflection coefficient
     * @return this Material object
     */
    public Material setKA(Double3 Ka) {
        this.Ka = Ka;
        return this;
    }

    /**
     * another setter for the ambient reflection coefficient.
     * @param Ka the ambient reflection coefficient
     * @return this Material object
     */
    public Material setKA(double Ka) {
        this.Ka = new Double3(Ka);
        return this;
    }
    /**
     * Setter for the diffuse reflection coefficient.
     * @param Kd the diffuse reflection coefficient
     * @return this Material object
     */
    public Material setKD(Double3 Kd) {
        this.Kd = Kd;
        return this;
    }
    /**
     * another setter for the diffuse reflection coefficient.
     * @param Kd the diffuse reflection coefficient
     * @return this Material object
     */
    public Material setKD(double Kd) {
        this.Kd = new Double3(Kd);
        return this;
    }
    /**
     * Setter for the specular reflection coefficient.
     * @param Ks the specular reflection coefficient
     * @return this Material object
     */
    public Material setKS(Double3 Ks) {
        this.Ks = Ks;
        return this;
    }
    /**
     * another setter for the specular reflection coefficient.
     * @param Ks the specular reflection coefficient
     * @return this Material object
     */
    public Material setKS(double Ks) {
        this.Ks = new Double3(Ks);
        return this;
    }

    /**
     * Setter for the transparency coefficient.
     * @param Kt the transparency coefficient
     * @return this Material object
     */
    public Material setKT(Double3 Kt) {
        this.Kt = Kt;
        return this;
    }
    /**
     * another setter for the transparency coefficient.
     * @param Kt the transparency coefficient
     * @return this Material object
     */
    public Material setKT(double Kt) {
        this.Kt = new Double3(Kt);
        return this;
    }
    /**
     * Setter for the refraction coefficient.
     * @param Kr the refraction coefficient
     * @return this Material object
     */
    public Material setKR(Double3 Kr) {
        this.Kr = Kr;
        return this;
    }
    /**
     * another setter for the refraction coefficient.
     * @param Kr the refraction coefficient
     * @return this Material object
     */
    public Material setKR(double Kr) {
        this.Kr = new Double3(Kr);
        return this;
    }
    /**
     * Setter for the shininess coefficient.
     * @param nSh the shininess coefficient
     * @return this Material object
     */
    public Material setShininess(int nSh) {
        this.nSh = nSh;
        return this;
    }

}
