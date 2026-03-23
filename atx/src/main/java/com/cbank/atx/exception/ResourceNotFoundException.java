package com.cbank.atx.exception;

// Exception pour les ressources
// non trouvées
// → utilisée quand un document
//   n'existe pas dans MongoDB
public class ResourceNotFoundException
        extends RuntimeException {

    public ResourceNotFoundException(
            String message) {
        super(message);
    }
}