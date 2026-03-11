# web-author-custom-user-mentions

**Requires Web Author v28.1+**

This plugin replaces the default user mention proposals from review comments with a custom provider.

When a user types `@` in an Add/Edit Comment dialog, the proposals come from
`sync.api.author.UserMentionProposalsProvider` installed on
`sync.api.author.ReviewCommentsManager`.

The sample provider returns grouped users:
- `Recent Collaborators`
- `Team`

It also includes duplicate usernames with different emails so the mention list shows merged
entries with all associated emails.

## Screenshot

![Custom user mentions dropdown](img/image.png)
