package io.lp0onfire.fireband;

public enum TileFlag {
  TILE_ALLOCATED, // Reserved by algorithm but not yet populated
  // Flags for graph algorithms (grey = visiting, black = visited)
  TILE_GREY,
  TILE_BLACK,
}
