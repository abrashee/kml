import { test, expect } from '@playwright/test';

test('mobile customer shell renders core navigation', async ({ page }) => {
  await page.setViewportSize({ width: 390, height: 844 });
  await page.route('**/api/v1/**', async route => {
    const url = route.request().url();
    if (url.includes('/auth/')) {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({ accessToken: 'stub', refreshToken: 'stub', tokenType: 'Bearer' }),
      });
      return;
    }

    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ content: [], totalPages: 0, totalElements: 0, number: 0, size: 10 }),
    });
  });
  await page.goto('/');

  await expect(page.getByRole('banner')).toBeVisible();
  await expect(page.locator('body > #root')).toBeVisible();
  await expect(page.locator('body')).not.toHaveCSS('overflow', 'hidden');
});
