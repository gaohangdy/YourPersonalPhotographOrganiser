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
package gallery.beans;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import gallery.database.entities.Photograph;
import gallery.images.PhotoMetadata;
import gallery.images.PhotoTag;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author maartenl
 */
@Stateless
@Path("/photographs")
public class PhotographBean extends AbstractBean<Photograph>
{

    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;

    protected EntityManager getEntityManager()
    {
        return em;
    }

    public PhotographBean()
    {
        super(Photograph.class);
    }

    @POST
    @Override
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public void create(Photograph entity)
    {
        super.create(entity);
    }

    @PUT
    @Override
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public void edit(Photograph entity)
    {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id)
    {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public Photograph find(@PathParam("id") Long id)
    {
        return super.find(id);
    }

    @GET
    @Override
    @Produces(
    {
        "application/xml", "application/json"
    })
    public List<Photograph> findAll()
    {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public List<Photograph> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to)
    {
        return super.findRange(new int[]
                {
                    from, to
                });
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST()
    {
        return String.valueOf(super.count());
    }

    public File getFile(Long id)
    {
        Photograph photo = find(id);
        if (photo == null)
        {
            return null;
        }

        java.nio.file.Path newPath = FileSystems.getDefault().getPath(photo.getLocationId().getFilepath(), photo.getRelativepath(), photo.getFilename());
        File file = newPath.toFile();
        return file;
    }

    @GET
    @Path("{id}/metadata")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public List<PhotoMetadata> getMetadata(@PathParam("id") Long id)
    {
        File jpegFile = getFile(id);
        Metadata metadata;
        try
        {
            // JDK7 : empty diamond
            List<PhotoMetadata> metadatas = new ArrayList<>();
            metadata = ImageMetadataReader.readMetadata(jpegFile);
            for (Directory directory : metadata.getDirectories())
            {
                PhotoMetadata mymetadata = new PhotoMetadata();
                mymetadata.name = directory.getName();

                mymetadata.taken = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                for (Tag tag : directory.getTags())
                {
                    mymetadata.tags.add(new PhotoTag(tag.getTagName(),
                            tag.getDescription()));
                }
                metadatas.add(mymetadata);
            }
            return metadatas;
            // JDK 7 - Multicatch , woohoo!
        } catch (ImageProcessingException | IOException ex)
        {
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        }

    }
}
