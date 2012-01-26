/*
 *  Copyright (C) 2012 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gallery.database.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "Photograph")
@XmlRootElement
@NamedQueries(
{
    @NamedQuery(name = "Photograph.findAll", query = "SELECT p FROM Photograph p"),
    @NamedQuery(name = "Photograph.findById", query = "SELECT p FROM Photograph p WHERE p.id = :id"),
    @NamedQuery(name = "Photograph.findByFilename", query = "SELECT p FROM Photograph p WHERE p.filename = :filename"),
    @NamedQuery(name = "Photograph.findByRelativepath", query = "SELECT p FROM Photograph p WHERE p.relativepath = :relativepath"),
    @NamedQuery(name = "Photograph.findByTaken", query = "SELECT p FROM Photograph p WHERE p.taken = :taken")
})
public class Photograph implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "filename")
    private String filename;
    @Size(max = 1024)
    @Column(name = "relativepath")
    private String relativepath;
    @Basic(optional = false)
    @NotNull
    @Column(name = "taken")
    @Temporal(TemporalType.TIMESTAMP)
    private Date taken;
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Location locationId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "photograph")
    private Collection<Tag> tagCollection;

    public Photograph()
    {
    }

    public Photograph(Long id)
    {
        this.id = id;
    }

    public Photograph(Long id, Date taken)
    {
        this.id = id;
        this.taken = taken;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getRelativepath()
    {
        return relativepath;
    }

    public void setRelativepath(String relativepath)
    {
        this.relativepath = relativepath;
    }

    public Date getTaken()
    {
        return taken;
    }

    public void setTaken(Date taken)
    {
        this.taken = taken;
    }

    public Location getLocationId()
    {
        return locationId;
    }

    public void setLocationId(Location locationId)
    {
        this.locationId = locationId;
    }

    @XmlTransient
    public Collection<Tag> getTagCollection()
    {
        return tagCollection;
    }

    public void setTagCollection(Collection<Tag> tagCollection)
    {
        this.tagCollection = tagCollection;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Photograph))
        {
            return false;
        }
        Photograph other = (Photograph) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "gallery.database.entities.Photograph[ id=" + id + " ]";
    }

}
