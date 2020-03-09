/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author labcisco
 */
@Entity
@Table(name = "barco")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Barco.findAll", query = "SELECT b FROM Barco b")
    , @NamedQuery(name = "Barco.findByIdBarco", query = "SELECT b FROM Barco b WHERE b.idBarco = :idBarco")
    , @NamedQuery(name = "Barco.findByMatricula", query = "SELECT b FROM Barco b WHERE b.matricula = :matricula")
    , @NamedQuery(name = "Barco.findByNombre", query = "SELECT b FROM Barco b WHERE b.nombre = :nombre")
    , @NamedQuery(name = "Barco.findByNumAmarre", query = "SELECT b FROM Barco b WHERE b.numAmarre = :numAmarre")
    , @NamedQuery(name = "Barco.findByCuota", query = "SELECT b FROM Barco b WHERE b.cuota = :cuota")})
public class Barco implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idBarco")
    private Integer idBarco;
    @Basic(optional = false)
    @Column(name = "matricula")
    private String matricula;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "numAmarre")
    private int numAmarre;
    @Basic(optional = false)
    @Column(name = "cuota")
    private double cuota;
    @JoinColumn(name = "idSocio", referencedColumnName = "idSocio")
    @ManyToOne
    private Socio idSocio;

    public Barco() {
    }

    public Barco(Integer idBarco) {
        this.idBarco = idBarco;
    }

    public Barco(Integer idBarco, String matricula, String nombre, int numAmarre, double cuota) {
        this.idBarco = idBarco;
        this.matricula = matricula;
        this.nombre = nombre;
        this.numAmarre = numAmarre;
        this.cuota = cuota;
    }

    public Integer getIdBarco() {
        return idBarco;
    }

    public void setIdBarco(Integer idBarco) {
        this.idBarco = idBarco;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumAmarre() {
        return numAmarre;
    }

    public void setNumAmarre(int numAmarre) {
        this.numAmarre = numAmarre;
    }

    public double getCuota() {
        return cuota;
    }

    public void setCuota(double cuota) {
        this.cuota = cuota;
    }

    public Socio getIdSocio() {
        return idSocio;
    }

    public void setIdSocio(Socio idSocio) {
        this.idSocio = idSocio;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idBarco != null ? idBarco.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Barco)) {
            return false;
        }
        Barco other = (Barco) object;
        if ((this.idBarco == null && other.idBarco != null) || (this.idBarco != null && !this.idBarco.equals(other.idBarco))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Barco[ idBarco=" + idBarco + " ]";
    }
    
}
