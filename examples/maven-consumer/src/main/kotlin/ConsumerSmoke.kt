import dev.skbindas.jev.modules.ModerationAction
import dev.skbindas.jev.modules.ModerationContext
import dev.skbindas.jev.modules.PaywallAction
import dev.skbindas.jev.modules.PaywallContext
import dev.skbindas.jev.modules.SearchCandidate

fun compilePublishedApi(
    paywall: PaywallContext,
    moderation: ModerationContext,
    candidate: SearchCandidate
): Set<Any> = setOf(
    paywall.isEntitled,
    moderation.text,
    candidate.id,
    PaywallAction.SUPPRESS,
    ModerationAction.REVIEW
)
