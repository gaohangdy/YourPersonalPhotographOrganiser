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
package gallery.enums;

/**
 *
 * @author maartenl
 */
public enum ImageSizeEnum
{

    BIG(), MEDIUM(350, 350), THUMB(100, 100), LARGE(1024,1024);

    private Integer maxHeight;
    private Integer maxWidth;

    private ImageSizeEnum()
    {
    }

    private ImageSizeEnum(Integer width, Integer height)
    {
        this.maxHeight = height;
        this.maxWidth = width;
    }

    public Integer getHeight()
    {
        return maxHeight;
    }

    public Integer getWidth()
    {
        return maxWidth;
    }
}
