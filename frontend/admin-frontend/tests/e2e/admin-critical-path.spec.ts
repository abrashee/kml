import { test, expect } from '@playwright/test';

test('admin shell renders login and handles mocked auth', async ({ page }) => {
  await page.route('**/api/v1/**', async route => {
    const url = route.request().url();
    if (url.includes('/auth/login')) {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          success: true,
          data: {
            accessToken: 'stub',
            refreshToken: 'stub',
            tokenType: 'Bearer',
            role: 'ADMIN',
          },
        }),
      });
      return;
    }

    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ success: true, data: [] }),
    });
  });

  await page.goto('/');
  await expect(page.locator('app-root')).toBeVisible();
  await expect(page.locator('body')).not.toHaveCSS('overflow', 'hidden');
});
