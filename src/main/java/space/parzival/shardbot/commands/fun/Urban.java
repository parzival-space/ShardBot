package space.parzival.shardbot.commands.fun;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import space.parzival.discord.shared.base.exceptions.CommandExecutionException;
import space.parzival.shardbot.modules.urban.UrbanDictionary;
import space.parzival.shardbot.modules.urban.model.DefinitionData;
import space.parzival.discord.shared.base.types.Command;
import space.parzival.discord.shared.base.types.RichEmbedBuilder;

@Component
public class Urban extends Command {

    @Autowired
    private UrbanDictionary dictionary;
    
    public Urban() {
        super("urban", "Search for word definitions using Urban Dictionary.");

        super.options.add(new OptionData(OptionType.STRING, "search", "The term you want to search.", true));
    }


    @Override
    public void execute(JDA client, SlashCommandInteractionEvent event, InteractionHook hook) throws CommandExecutionException {

        // read the option
        @SuppressWarnings("null")
        String term = event.getOption("search").getAsString();

        // search for definitions
        List<DefinitionData> definitions = this.dictionary.getDefinitions(term);

        if (definitions.isEmpty()) {
            hook.sendMessageEmbeds(
                RichEmbedBuilder.simple("Sorry, it looks like there are no definitions for that term.").build()
            ).queue();
            return;
        }

        // send message
        MessageEmbed message = new RichEmbedBuilder()
            .setTitle(definitions.get(0).getWord())
            .setDescription(makeSentenceableString(definitions.get(0).getDefinition()))
            .setUrl(definitions.get(0).getPermalink())
            .addField("Example", definitions.get(0).getExample(), false)
            .setFooter("Definition provided by Urban Dictionary", null, null)
            .setColor(0x1b2936)
            .build();

        hook.sendMessageEmbeds(message).queue();
    }

    /**
     * Formats string a little more readable.
     */
    private String makeSentenceableString(String original) {
        StringBuilder builder = new StringBuilder();

        builder.append(original.substring(0, 1).toUpperCase());
        builder.append(original.substring(1));

        if (!original.endsWith(".")) builder.append(".");

        return builder.toString();
    }
}
