/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.girod.ontobrowser.model.restriction;

import org.apache.jena.ontology.Restriction;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL2;

/**
 * An owl restriction which binds to a max qualified cardinality restriction.
 *
 * @since 0.1
 */
public class OwlMaxQualifiedCardinalityRestriction extends RestrictedOwlRestriction<Restriction> {
   private final int maxCardinality;

   public OwlMaxQualifiedCardinalityRestriction(Restriction restriction, Resource resource) {
      super(restriction);
      computeKey(resource);
      
      RDFNode node = restriction.getPropertyValue(OWL2.maxQualifiedCardinality);
      Literal literal = node.asLiteral();
      maxCardinality = literal.getInt();
   }
   
   /**
    * Return the max cardinality.
    *
    * @return the max cardinality
    */
   public int getMaxCardinality() {
      return maxCardinality;
   }   
}

