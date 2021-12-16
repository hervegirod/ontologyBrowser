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
 * An owl restriction which binds to a min qualified cardinality restriction.
 *
 * @since 0.1
 */
public class OwlMinQualifiedCardinalityRestriction extends RestrictedOwlRestriction<Restriction> {
   private final int minCardinality;

   public OwlMinQualifiedCardinalityRestriction(Restriction restriction, Resource resource) {
      super(restriction);
      computeKey(resource);
      
      RDFNode node = restriction.getPropertyValue(OWL2.minQualifiedCardinality);
      Literal literal = node.asLiteral();
      minCardinality = literal.getInt();      
   }
   
   /**
    * Return the min cardinality.
    *
    * @return the min cardinality
    */
   public int getMinCardinality() {
      return minCardinality;
   }     
}

