package net.greensnet.domain.fixture;

import lombok.Builder;
import lombok.Data;
import net.greensnet.domain.GenericAppearance;
import net.greensnet.domain.OppositionAppearance;

import java.util.Optional;

@Data
@Builder
public class FixtureEvent implements Comparable<FixtureEvent> {
    private EventType type;
    private int minute;
    private GenericAppearance player;
    private GenericAppearance player2;
    private boolean hendonEvent;

    @Override
    public int compareTo(FixtureEvent o) {
        return Integer.compare(this.minute, o.minute);
    }

    public String getDisplayInfo() {
        OppositionAppearance unknown = OppositionAppearance.builder().firstName("").lastName("Unknown").build();

        if (type.equals(EventType.SUBSTITUTION)) {
            return Optional.ofNullable(player).orElse(unknown).getDisplayName() + " replaced " + Optional.ofNullable(player2).orElse(unknown).getDisplayName();
        }
        return Optional.ofNullable(player).orElse(unknown).getDisplayName();
    }
}
