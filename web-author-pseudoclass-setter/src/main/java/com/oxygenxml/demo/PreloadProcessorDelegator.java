package com.oxygenxml.demo;

import java.util.ArrayList;
import java.util.List;

import ro.sync.ecss.extensions.api.AuthorPreloadProcessor;
import ro.sync.ecss.extensions.api.AuthorPseudoClassController;
import ro.sync.ecss.extensions.api.node.AuthorDocument;

/**
 * A class that delegates preload processing to multiple AuthorPreloadProcessor instances.
 */
public class PreloadProcessorDelegator implements AuthorPreloadProcessor {
    
    /**
     * The list of processors to delegate to.
     */
    private List<AuthorPreloadProcessor> processors = new ArrayList<>();
    
    /**
     * Adds a processor to the delegation chain.
     * 
     * @param processor The processor to add
     * @return This delegator instance for chaining
     */
    public PreloadProcessorDelegator addProcessor(AuthorPreloadProcessor processor) {
        if (processor != null) {
            processors.add(processor);
        }
        return this;
    }
    
    @Override
    public void documentAboutToBeLoaded(AuthorDocument document, AuthorPseudoClassController pseudoClassController) {
        // Call each processor in sequence
        for (AuthorPreloadProcessor processor : processors) {
            processor.documentAboutToBeLoaded(document, pseudoClassController);
        }
    }
}