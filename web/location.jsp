<%--
Document : index
Created on : Dec 18, 2011, 11:46:17 AM
Author : maartenl
--%>
<%@ page import="gallery.database.entities.Photograph"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.nio.file.Files"%>
<%@ page import="java.nio.file.FileSystems"%>
<%@ page import="java.nio.file.FileVisitResult"%>
<%@ page import="java.nio.file.FileVisitor"%>
<%@ page import="java.nio.file.Path"%>
<%@ page import="java.nio.file.PathMatcher"%>
<%@ page import="java.nio.file.attribute.BasicFileAttributes"%>
<%@ page import="java.nio.file.attribute.PosixFileAttributes"%>
<%@ page import="java.nio.file.LinkOption"%>

<%@ page import="java.util.logging.Level"%>
<%@ page import="java.util.logging.Logger"%>
<%@ page import="javax.naming.InitialContext"%>
<%@ page import="javax.naming.NamingException"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Your Personal Photograph Organiser</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
        <script type="text/javascript" src="/fancybox/jquery.fancybox-1.3.4.pack.js"></script>
        <script type="text/javascript" src="/fancybox/jquery.mousewheel-3.0.4.pack.js"></script>
        <script type="text/javascript">

            var YourPersonalPhotographOrganiserBag = {
                debug : true, // debugging=false is debugging off
                index : 0
            };

            function createLocation()
            {
                alert("createLocation");
                var location = {
                    filepath:$("#filepath").val()
                };
                if (window.console && YourPersonalPhotographOrganiserBag.debug)
                {
                    console.debug(location);
                }
                $.ajax({
                    type: "POST",
                    url: "/YourPersonalPhotographOrganiser/resources/locations",
                    data: JSON.stringify(location),
                    success: function()
                    {
                        alert("Success!");
                    },
                    contentType: "application/json"
                }).done(function( msg ) {
                    alert( "Data Saved: " + msg );
                });
            }

            function updateLocation()
            {
                var location = YourPersonalPhotographOrganiserBag.locations[YourPersonalPhotographOrganiserBag.index];
                if (location == null)
                {
                    return;
                }
                location.id = $('#locationid').html();
                location.filepath= $('#filepath').val();
                $.ajax({
                    type: "PUT",
                    url: "/YourPersonalPhotographOrganiser/resources/locations",
                    data: JSON.stringify(location),
                    success: function()
                    {
                        // alert("Success!");
                    },
                    contentType: "application/json"
                }).done(function( msg ) {
                    // alert( "Data Saved: " + msg );
                });
            }

            function deleteLocation()
            {
                alert("deleteLocation");
                var id = YourPersonalPhotographOrganiserBag.locations[YourPersonalPhotographOrganiserBag.index].id;
                $.ajax({
                    type: "DELETE",
                    url: "/YourPersonalPhotographOrganiser/resources/locations/" + id,
                    success: function()
                    {
                        alert("Success!");
                    },
                    contentType: "application/json"
                }).done(function( msg ) {
                    alert( "Data Saved: " + msg );
                });
            }

            function refreshPage()
            {
                $.get(
                '/YourPersonalPhotographOrganiser/resources/locations'
                ,
                function(data)
                {
                    if (window.console && YourPersonalPhotographOrganiserBag.debug)
                    {
                        console.debug(data);
                    }
                    if (data == null)
                    {
                        alert("No locations found.");
                        return;
                    }
                    YourPersonalPhotographOrganiserBag.locations = data;
                    var buffer="<table><tr><th>id</th><th>filepath</th></tr>";
                    for (i in data)
                    {
                        buffer+="<tr><td><a onclick=\"YourPersonalPhotographOrganiserBag.index="+i+";\">" + data[i].id + "</a></td><td>" + data[i].filepath + "</td></tr>";
                    }
                    buffer+="</table>";
                    $("#locations").html(buffer);
                    var currentLocation = data[YourPersonalPhotographOrganiserBag.index];
                    $('#locationid').html(currentLocation.id);
                    $('#filepath').val(currentLocation.filepath);
                } // end function data
                , "json"); // endget locationphotograph
                // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )

            }

            $(document).ready(function()
            {
                refreshPage();

                $('.refreshLocation').click(function(){refreshPage();});
                $('.createLocation').click(function(){createLocation();});
                $('.updateLocation').click(function(){updateLocation();});
                $('.deleteLocation').click(function(){deleteLocation();});

            }); // end document ready
        </script>
        <link rel="stylesheet" href="/fancybox/jquery.fancybox-1.3.4.css" type="text/css" media="screen" />
        <link rel="stylesheet" href="css/yppo.css" type="text/css" media="screen" />
    </head>
    <body>
        <h1>Locations</h1>
        <hr/>
        <div id="locations"></div>
        <p>Location information</p>
        <p>Location id: <span id="locationid"></span></p>
        <label for="name">Filepath</label>
        <input type="text" name="filepath" id="filepath"/>
        <br/>
        <hr/>
        <div class="refreshLocation myButton">Refresh</div>
        <div class="createLocation myButton">Create</div>
        <div class="updateLocation myButton">Update</div>
        <div class="deleteLocation myButton">Delete</div>
        <br/>
        <a href="gallery.jsp">Galleries</a>
        <a href="index.jsp" >Return</a>
    </body>
</html>