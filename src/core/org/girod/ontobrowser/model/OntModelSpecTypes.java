/*
Copyright (c) 2023 Hervé Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://github.com/hervegirod/ontologyBrowser
 */
package org.girod.ontobrowser.model;

import org.apache.jena.ontology.OntModelSpec;

/**
 * Represents the types of model specification types.
 *
 * @since 0.8
 */
public interface OntModelSpecTypes {
   /**
    * A specification for OWL models that are stored in memory and use the RDFS inferencer for additional entailments.
    */
   public static String OWL_MEM_RDFS_INF = "OWL_MEM_RDFS_INF";
   /**
    * A specification for OWL DL models that are stored in memory and use the RDFS inferencer for additional entailments.
    */
   public static String OWL_DL_MEM_RDFS_INF = "OWL_DL_MEM_RDFS_INF";
   /**
    * A specification for OWL DL models that are stored in memory and use the OWL rules inference engine for additional entailments.
    */
   public static String OWL_DL_MEM_RULE_INF = "OWL_DL_MEM_RULE_INF";
   /**
    * A specification for OWL DL models that are stored in memory and use the transitive inferencer for additional entailments.
    */
   public static String OWL_DL_MEM_TRANS_INF = "OWL_DL_MEM_TRANS_INF";
   /**
    * A specification for OWL Lite models that are stored in memory and do no entailment additional reasoning.
    */
   public static String OWL_LITE_MEM = "OWL_LITE_MEM";
   /**
    * A specification for OWL Lite models that are stored in memory and use the RDFS inferencer for additional entailments.
    */
   public static String OWL_LITE_MEM_RDFS_INF = "OWL_LITE_MEM_RDFS_INF";
   /**
    * A specification for OWL Lite models that are stored in memory and use the OWL rules inference engine for additional entailments.
    */
   public static String OWL_LITE_MEM_RULES_INF = "OWL_LITE_MEM_RULES_INF";
   /**
    * A specification for OWL Lite models that are stored in memory and use the transitive inferencer for additional entailments.
    */
   public static String OWL_LITE_MEM_TRANS_INF = "OWL_LITE_MEM_TRANS_INF";
   /**
    * A specification for OWL models that are stored in memory and use the mini OWL rules inference engine for additional entailments.
    */
   public static String OWL_MEM_MINI_RULE_INF = "OWL_MEM_MINI_RULE_INF";
   /**
    * A specification for OWL models that are stored in memory and use the transitive inferencer for additional entailments.
    */
   public static String OWL_MEM_TRANS_INF = "OWL_MEM_TRANS_INF";
   /**
    * A specification for RDFS ontology models that are stored in memory and use the RDFS inferencer for additional entailments.
    */
   public static String RDFS_MEM_RDFS_INF = "RDFS_MEM_RDFS_INF";
   /**
    * A specification for RDFS ontology models that are stored in memory and use the transitive reasoner for entailments.
    */
   public static String RDFS_MEM_TRANS_INF = "RDFS_MEM_TRANS_INF";
   /**
    * A specification for OWL models that are stored in memory and use the micro OWL rules inference engine for additional entailments.
    */
   public static String OWL_MEM_MICRO_RULE_INF = "OWL_MEM_MICRO_RULE_INF";
   /**
    * A specification for OWL DL models that are stored in memory and do no additional entailment reasoning.
    */
   public static String OWL_DL_MEM = "OWL_DL_MEM";
   /**
    * A specification for OWL models that are stored in memory and do no additional entailment reasoning.
    */
   public static String OWL_MEM = "OWL_MEM";

   public static OntModelSpec getOntModelSpec(String spec) {
      switch (spec) {
         case OWL_MEM_RDFS_INF:
            return OntModelSpec.OWL_MEM_RDFS_INF;
         case OWL_DL_MEM_RDFS_INF:
            return OntModelSpec.OWL_DL_MEM_RDFS_INF;
         case OWL_DL_MEM_RULE_INF:
            return OntModelSpec.OWL_DL_MEM_RULE_INF;
         case OWL_DL_MEM_TRANS_INF:
            return OntModelSpec.OWL_DL_MEM_TRANS_INF;
         case OWL_LITE_MEM:
            return OntModelSpec.OWL_LITE_MEM;
         case OWL_LITE_MEM_RDFS_INF:
            return OntModelSpec.OWL_LITE_MEM_RDFS_INF;
         case OWL_LITE_MEM_RULES_INF:
            return OntModelSpec.OWL_LITE_MEM_RULES_INF;
         case OWL_LITE_MEM_TRANS_INF:
            return OntModelSpec.OWL_LITE_MEM_TRANS_INF;
         case OWL_MEM_MICRO_RULE_INF:
            return OntModelSpec.OWL_MEM_MICRO_RULE_INF;
         case OWL_MEM_MINI_RULE_INF:
            return OntModelSpec.OWL_MEM_MINI_RULE_INF;
         case OWL_MEM_TRANS_INF:
            return OntModelSpec.OWL_MEM_TRANS_INF;
         case RDFS_MEM_RDFS_INF:
            return OntModelSpec.RDFS_MEM_RDFS_INF;
         case RDFS_MEM_TRANS_INF:
            return OntModelSpec.RDFS_MEM_TRANS_INF;
         case OWL_DL_MEM:
            return OntModelSpec.OWL_DL_MEM;
         case OWL_MEM:
         default:
            return OntModelSpec.OWL_MEM;
      }
   }
}
