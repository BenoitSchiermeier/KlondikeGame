package cs3500.klondike.view;

import java.io.IOException;

/**
 * Represents a textual view for a game or model.
 * Provides a mechanism to render the model, abstracting away from purely String-based views.
 *
 * @author [Your Name or Team Name]
 */
public interface TextualView {

  /**
   * Renders a model in some manner (e.g. as text, or as graphics, etc.).
   * @throws IOException if the rendering fails for some reason
   */
  void render() throws IOException;

}
