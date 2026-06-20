import { type ReactNode, useEffect, useMemo, useRef, useState } from 'react';

type VirtualizedCardGridProps<T> = {
  items: readonly T[];
  minCardWidth?: number;
  cardHeight?: number;
  gap?: number;
  overscanRows?: number;
  renderItem: (item: T, index: number) => ReactNode;
};

export function VirtualizedCardGrid<T>({
  items,
  minCardWidth = 240,
  cardHeight = 280,
  gap = 24,
  overscanRows = 2,
  renderItem,
}: VirtualizedCardGridProps<T>) {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const [viewport, setViewport] = useState({ width: 0, height: 0, scrollTop: 0 });

  useEffect(() => {
    const element = containerRef.current;
    if (!element) {
      return;
    }

    const update = () => {
      setViewport({ width: element.clientWidth, height: element.clientHeight, scrollTop: element.scrollTop });
    };

    update();

    const resizeObserver = new ResizeObserver(update);
    resizeObserver.observe(element);
    element.addEventListener('scroll', update, { passive: true });

    return () => {
      resizeObserver.disconnect();
      element.removeEventListener('scroll', update);
    };
  }, []);

  const grid = useMemo(() => {
    const columnCount = Math.max(1, Math.floor((viewport.width + gap) / (minCardWidth + gap)));
    const rowCount = Math.ceil(items.length / columnCount);
    const rowHeight = cardHeight + gap;
    const visibleStartRow = Math.max(0, Math.floor(viewport.scrollTop / rowHeight) - overscanRows);
    const visibleEndRow = Math.min(rowCount, Math.ceil((viewport.scrollTop + viewport.height) / rowHeight) + overscanRows);
    const visibleItems = [] as Array<{ item: T; index: number; top: number }>;

    for (let row = visibleStartRow; row < visibleEndRow; row += 1) {
      for (let column = 0; column < columnCount; column += 1) {
        const index = row * columnCount + column;
        if (index >= items.length) {
          break;
        }

        visibleItems.push({
          item: items[index],
          index,
          top: row * rowHeight,
        });
      }
    }

    return {
      columnCount,
      rowCount,
      rowHeight,
      height: rowCount * rowHeight,
      visibleItems,
    };
  }, [cardHeight, gap, items, minCardWidth, overscanRows, viewport.height, viewport.scrollTop, viewport.width]);

  return (
    <div ref={containerRef} style={{ position: 'relative', overflowY: 'auto', minHeight: '60vh', maxHeight: '72vh' }}>
      <div style={{ position: 'relative', height: grid.height }}>
        {grid.visibleItems.map(({ item, index, top }) => (
          <div
            key={index}
            style={{
              position: 'absolute',
              top,
              left: 0,
              width: `calc(${100 / grid.columnCount}% - ${gap}px)`,
              paddingRight: gap,
              paddingBottom: gap,
              height: cardHeight,
              boxSizing: 'border-box',
            }}
          >
            {renderItem(item, index)}
          </div>
        ))}
      </div>
    </div>
  );
}
