/*
 * Demoiselle Framework
 *
 * License: GNU Lesser General Public License (LGPL), version 3 or later.
 * See the lgpl.txt file in the root directory or <https://www.gnu.org/licenses/lgpl.html>.
 */
package org.demoiselle.jee.crud;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.demoiselle.jee.core.api.crud.Result;
import org.demoiselle.jee.crud.fields.FieldHelper;
import org.demoiselle.jee.crud.filter.FilterHelper;
import org.demoiselle.jee.crud.pagination.PaginationHelper;
import org.demoiselle.jee.crud.sort.SortHelper;

/**
 * TODO javadoc
 *
 * @author SERPRO
 *
 */
@Provider
public class CrudFilter implements ContainerResponseFilter, ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;
	
	@Context
    private UriInfo uriInfo;

	@Inject
	private DemoiselleRequestContext drc;

	@Inject
	private PaginationHelper paginationHelper;
	
	@Inject
	private SortHelper sortHelper;
	
	@Inject
	private FilterHelper filterHelper;
	
	@Inject
	private FieldHelper fieldHelper;
	
	private static final Logger logger = Logger.getLogger(CrudFilter.class.getName());
	
    public CrudFilter() {}
    
    public CrudFilter(ResourceInfo resourceInfo, UriInfo uriInfo, DemoiselleRequestContext drc, PaginationHelper paginationHelper, SortHelper sortHelper, FilterHelper filterHelper){
        this.resourceInfo = resourceInfo;
        this.uriInfo = uriInfo;
        this.drc = drc;
        this.paginationHelper = paginationHelper;
        this.sortHelper = sortHelper;
        this.filterHelper = filterHelper;        
    }

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

	    if(isRequestForCrud()){
    	    try {
    	        paginationHelper.execute(resourceInfo, uriInfo);
    	        sortHelper.execute(resourceInfo, uriInfo);
    	        filterHelper.execute(resourceInfo, uriInfo);
    	        //fieldHelper.execute(resourceInfo, uriInfo);
    	    } 
    	    catch (IllegalArgumentException e) {
                throw new BadRequestException(e.getMessage());
            }
	    }
	}
	

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext response) throws IOException {

        if (response.getEntity() instanceof Result) {

            paginationHelper.buildHeaders().forEach((k, v) -> response.getHeaders().putSingle(k, v));

            response.setEntity(buildContent(response));

            if (!paginationHelper.isPartialContentResponse()) {
                response.setStatus(Status.OK.getStatusCode());
            } 
            else {
                response.setStatus(Status.PARTIAL_CONTENT.getStatusCode());
            }
        }
        else {
            if (Status.BAD_REQUEST.getStatusCode() == response.getStatus() && drc.getEntityClass() == null) {
                paginationHelper.buildAcceptRangeWithResponse(response);
            }
        }

    }
   
    private Boolean isRequestForCrud() {
        if(resourceInfo.getResourceClass().getSuperclass() != null
				&& resourceInfo.getResourceClass().getSuperclass().equals(AbstractREST.class)
                && resourceInfo.getResourceMethod().isAnnotationPresent(GET.class)){
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }
    
    private Object buildContent(ContainerResponseContext response){
        
        @SuppressWarnings("unchecked")
        List<Object> content = (List<Object>) ((Result) response.getEntity()).getContent();
        
        try{
            //Validate if fields exists on fields field from @Search annotation
            if(resourceInfo.getResourceMethod().isAnnotationPresent(Search.class)){
                content = new ArrayList<>();
                Class<?> targetClass = CrudUtilHelper.getTargetClass(resourceInfo.getResourceClass());
                Search search = resourceInfo.getResourceMethod().getAnnotation(Search.class);
                List<String> searchFields = Arrays.asList(search.fields());
                
                for(Object object : ((Result) response.getEntity()).getContent()){
                    Map<String, Object> keyValue = new HashMap<>();
                    
                    for(Field field : object.getClass().getDeclaredFields()){
                        if(searchFields.contains(field.getName())){
                            Field actualField = targetClass.getDeclaredField(field.getName());                            
                            boolean acessible = actualField.isAccessible();
                            actualField.setAccessible(true);                                
                            keyValue.put(field.getName(), actualField.get(object));                                
                            actualField.setAccessible(acessible);
                            
                        }
                    }
                    
                    content.add(keyValue);
                }
                
            }
            
        }
        catch(IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e){
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        
        return content;
        
    }

    
}