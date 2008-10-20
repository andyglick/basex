package org.basex.api.xmldb;

import java.util.HashMap;
import org.basex.data.Nodes;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.xpath.XPathProcessor;
import org.basex.query.xquery.XQueryProcessor;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

/**
 * Abstract QueryService definition for the XMLDB:API.
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Andreas Weiler
 */
public final class BXQueryService implements XPathQueryService, BXXMLDBText {
  /** XPath service constant. */
  static final String XPATH = "XPathQueryService";
  /** XQuery service constant. */
  static final String XQUERY = "XQueryQueryService";

  /** Namespaces. */
  private final HashMap<String, String> ns = new HashMap<String, String>();
  /** Service name. */
  private final String name;
  /** Service version. */
  private final String version;
  /** Collection reference. */
  private BXCollection coll;

  /**
   * Standard constructor.
   * @param c for collection reference
   * @param n service name
   * @param v version
   */
  public BXQueryService(final BXCollection c, final String n, final String v) {
    coll = c;
    name = n;
    version = v;
  }

  public void setNamespace(final String pre, final String uri)
      throws XMLDBException {

    if(uri != null && uri.length() != 0) ns.put(pre == null ? "" : pre, uri);
    else throw new XMLDBException(ErrorCodes.VENDOR_ERROR, ERR_NSURI + pre);
  }

  public String getNamespace(String pre) {
    return ns.get(pre == null ? "" : pre);
  }

  public void removeNamespace(final String pre) {
    ns.remove(pre == null ? "" : pre);
  }

  public void clearNamespaces() {
    ns.clear();
  }

  public ResourceSet query(final String query) throws XMLDBException {
    return query(coll.ctx.current(), query);
  }

  public ResourceSet queryResource(final String id, final String query)
      throws XMLDBException {

    final BXXMLResource xml = (BXXMLResource) coll.getResource(id);
    if(xml != null) return query(new Nodes(xml.pos, xml.data), query);
    // throw exception if id was not found...
    throw new XMLDBException(ErrorCodes.VENDOR_ERROR, ERR_RES + id);
  }
  
  /**
   * Method for both query actions.
   * @param nodes initial node set
   * @param query query string
   * @return resource set
   * @throws XMLDBException exception
   */
  private ResourceSet query(final Nodes nodes, final String query)
      throws XMLDBException {
    
    try {
      // creates a query instance
      final QueryProcessor proc = name.equals(XPATH) ?
          new XPathProcessor(query) : new XQueryProcessor(query);

      // [CG] consider namespaces

      // perform query and return result
      return new BXResourceSet(proc.query(nodes), coll);
    } catch(final QueryException ex) {
      
      throw new XMLDBException(ErrorCodes.VENDOR_ERROR, ex.getMessage());
    }
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public void setCollection(final Collection col) {
    coll = (BXCollection) col;
  }

  public String getProperty(final String nm) {
    return null;
  }

  public void setProperty(final String nm, final String value)
      throws XMLDBException {
    throw new XMLDBException(ErrorCodes.VENDOR_ERROR, ERR_PROP + nm);
  }
}
