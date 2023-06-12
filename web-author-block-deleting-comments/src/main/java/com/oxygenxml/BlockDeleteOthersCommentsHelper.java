package com.oxygenxml;

import ro.sync.ecss.extensions.api.AuthorReviewController;
import ro.sync.ecss.extensions.api.highlights.AuthorPersistentHighlight;
import ro.sync.ecss.extensions.api.highlights.AuthorPersistentHighlightConstants;
import ro.sync.ecss.extensions.api.highlights.AuthorPersistentHighlight.PersistentHighlightType;

/**
 * Helper for deciding if a comment can be deleted.
 *
 */
public class BlockDeleteOthersCommentsHelper {

  private AuthorReviewController reviewController;

  /**
   * @param reviewController Author review controller.
   */
  public BlockDeleteOthersCommentsHelper(AuthorReviewController reviewController) {
    this.reviewController = reviewController;
  }
  
  /**
   * Check if a marker is related to others users comments
   * 
   * @param marker A persistent marker.
   * @return <code>true</code>if the marker is related to others users comments
   */
  public boolean isMarkerRelatedToOthersComments(AuthorPersistentHighlight marker) {
    return isComment(marker) && (isAddedByOtherUser(marker) || hasRepliesFromOthers(marker));
  }

  /**
   * Check if a content interval contains comments from other users
   * 
   * @param startOffset Interval start offset
   * @param endOffset Interval end offset
   * @return <code>true</code> if the content interval contains comments from other users
   */
  public boolean isContentCommentedByOthers(int startOffset, int endOffset) {
    boolean hasOthersComments = false;
    AuthorPersistentHighlight[] commentHighlights = getCommentHighlights(startOffset, endOffset);
    for (AuthorPersistentHighlight comment : commentHighlights) {
      if (isAddedByOtherUser(comment)) {
        hasOthersComments = true;
        break;
      }
    }
    return hasOthersComments;
  }
  
  /**
   * Check if multiple intervals contain comments from other users
   * 
   * @param startOffsets Intervals starts offsets
   * @param endOffsets Intervals end offsets
   * @return <code>true</code> if the interval can be changed or deleted
   */
  public boolean isContentCommentedByOthers(int[] startOffsets, int[] endOffsets) {
    boolean hasOthersComments = false;
    int intervalsCount = startOffsets.length;
    for (int i = 0; i < intervalsCount; i++) {
      if (isContentCommentedByOthers(startOffsets[i], endOffsets[i])) {
        hasOthersComments = true;
        break;
      }
    }
    return hasOthersComments;
  }
  
  private AuthorPersistentHighlight[] getCommentHighlights(int startOffset, int endOffset) {
    AuthorPersistentHighlight[] commentHighlights = reviewController.getCommentHighlights(startOffset, endOffset);
    return commentHighlights == null ? new AuthorPersistentHighlight[0] : commentHighlights;
  }
  
  private boolean isAddedByOtherUser(AuthorPersistentHighlight marker) {
    return !reviewController.getReviewerAuthorName().equals(getAuthorName(marker));
  }

  private boolean hasRepliesFromOthers(AuthorPersistentHighlight marker) {
    boolean hasReplysFromOthers = false;

    String commentId = getCommentId(marker);
    if (commentId != null) {
      AuthorPersistentHighlight[] commentHighlights = getCommentHighlights(marker.getStartOffset(), marker.getEndOffset());
      for (AuthorPersistentHighlight comment : commentHighlights) {
        boolean isReplyOfMarker = commentId.equals(getCommentParentId(comment));
        if (isReplyOfMarker && (isAddedByOtherUser(comment) || hasRepliesFromOthers(comment))) {
          hasReplysFromOthers = true;
          break;
        } 
      }
    }
    return hasReplysFromOthers;
  }

  private String getCommentId(AuthorPersistentHighlight marker) {
    return marker.getProperty(AuthorPersistentHighlightConstants.COMMENT_ID);
  }
  
  private String getCommentParentId(AuthorPersistentHighlight marker) {
    return marker.getProperty(AuthorPersistentHighlightConstants.COMMENT_PARENT_ID);
  }
  
  private boolean isComment(AuthorPersistentHighlight marker) {
    return marker.getType() == PersistentHighlightType.COMMENT;
  }

  private String getAuthorName(AuthorPersistentHighlight marker) {
    return marker.getProperty(AuthorPersistentHighlightConstants.AUTHOR_NAME_ATTRIBUTE);
  }

}
