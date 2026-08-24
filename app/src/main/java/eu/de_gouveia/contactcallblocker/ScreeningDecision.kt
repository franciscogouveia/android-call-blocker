package eu.de_gouveia.contactcallblocker

internal enum class CallDirection {
    INCOMING,
    OUTGOING,
    UNKNOWN,
}

internal enum class ScreeningAction {
    BLOCK,
    IGNORE,
}

internal fun screeningAction(direction: CallDirection): ScreeningAction =
    if (direction == CallDirection.INCOMING) ScreeningAction.BLOCK else ScreeningAction.IGNORE
