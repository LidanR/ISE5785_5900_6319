package primitives;

public class Material {
    // Ambient reflection coefficient
    public Double3 Ka = Double3.ONE;
    // Diffuse reflection coefficient
    public Double3 Kd = Double3.ZERO;
    // Specular reflection coefficient
    public Double3 Ks = Double3.ZERO;
    // Shininess coefficient
    public int nSh = 0;

    /**
     * Setter for the ambient reflection coefficient.
     * @param Ka the ambient reflection coefficient
     * @return this Material object
     */
    public Material setKa(Double3 Ka) {
        this.Ka = Ka;
        return this;
    }

    /**
     * another setter for the ambient reflection coefficient.
     * @param Ka the ambient reflection coefficient
     * @return this Material object
     */
    public Material setKa(double Ka) {
        this.Ka = new Double3(Ka);
        return this;
    }
    /**
     * Setter for the diffuse reflection coefficient.
     * @param Kd the diffuse reflection coefficient
     * @return this Material object
     */
    public Material setKd(Double3 Kd) {
        this.Kd = Kd;
        return this;
    }
    /**
     * another setter for the diffuse reflection coefficient.
     * @param Kd the diffuse reflection coefficient
     * @return this Material object
     */
    public Material setKd(double Kd) {
        this.Kd = new Double3(Kd);
        return this;
    }
    /**
     * Setter for the specular reflection coefficient.
     * @param Ks the specular reflection coefficient
     * @return this Material object
     */
    public Material setKs(Double3 Ks) {
        this.Ks = Ks;
        return this;
    }
    /**
     * another setter for the specular reflection coefficient.
     * @param Ks the specular reflection coefficient
     * @return this Material object
     */
    public Material setKs(double Ks) {
        this.Ks = new Double3(Ks);
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
